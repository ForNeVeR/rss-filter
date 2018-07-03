module RssFilter.Server

open System
open System.IO

open System.Text.RegularExpressions
open System.Xml.Linq
open Giraffe
open FSharp.Control.Tasks.ContextInsensitive
open Microsoft.AspNetCore.Builder
open Microsoft.AspNetCore.Hosting
open Microsoft.AspNetCore.Http
open Microsoft.Extensions.Configuration
open Microsoft.Extensions.Logging
open Microsoft.Extensions.Options
open Microsoft.Extensions.DependencyInjection

[<CLIMutable>]
type FeedSettings =
    { name : string
      url : string
      titleFilter : string }

[<CLIMutable>]
type RssFilterSettings =
    { rss : FeedSettings seq }

let private notFound = setStatusCode 404 >=> text "Not Found"

let private getFeedSettings (ctx : HttpContext) feedName =
    let settings = ctx.GetService<IOptions<RssFilterSettings>>()
    settings.Value.rss
    |> Seq.filter(fun feed -> feed.name = feedName)
    |> Seq.tryHead

let private rss(content : Domain.Feed) : HttpHandler =
    setHttpHeader "Content-Type" "application/rss+xml; charset=utf-8"
    >=> setBodyFromString(Domain.serialize content)

let private feedHandler(feedName : string) : HttpHandler =
    fun next (ctx : HttpContext) ->
        let feed = getFeedSettings ctx feedName
        task {
            match feed with
            | Some feed ->
                let! content = Domain.readFeed (Uri feed.url) ctx.RequestAborted
                let filteredContent = Domain.filterFeed (Regex feed.titleFilter) content
                return! rss filteredContent next ctx
            | None ->
                return! notFound next ctx
        }

let private webApp =
    choose [
        GET >=>
            choose [
                routef "/feed/%s" feedHandler
            ]
        notFound ]

let private errorHandler (ex : Exception) (logger : ILogger) =
    logger.LogError(EventId(), ex, "An unhandled exception has occurred while executing the request.")
    clearResponse >=> setStatusCode 500 >=> text ex.Message

let private configureApp(app : IApplicationBuilder) =
    let env = app.ApplicationServices.GetService<IHostingEnvironment>()
    (match env.IsDevelopment() with
    | true  -> app.UseDeveloperExceptionPage()
    | false -> app.UseGiraffeErrorHandler errorHandler)
        .UseGiraffe(webApp)

let private configureServices (config : IConfiguration) (services : IServiceCollection) =
    ignore <| services.AddGiraffe()
    ignore <| services.AddOptions()
    ignore <| services.Configure<RssFilterSettings>(config)

let private configureLogging (builder : ILoggingBuilder) =
    let filter (l : LogLevel) = l.Equals LogLevel.Error
    builder.AddFilter(filter).AddConsole().AddDebug() |> ignore

[<EntryPoint>]
let main _ =
    let configuration =
        ConfigurationBuilder()
            .AddJsonFile(Path.Combine(Directory.GetCurrentDirectory(), "appsettings.json"))
            .Build()
    WebHostBuilder()
        .UseKestrel()
        .Configure(Action<IApplicationBuilder> configureApp)
        .ConfigureServices(configureServices configuration)
        .ConfigureLogging(configureLogging)
        .UseConfiguration(configuration)
        .Build()
        .Run()
    0
