module RssFilter.Domain

open System
open System.Text.RegularExpressions
open System.Threading
open System.Threading.Tasks
open System.Xml
open System.Xml.Linq
open System.Xml.XPath

open FSharp.Control.Tasks.ContextInsensitive

type Feed = XDocument

let readFeed(uri : Uri) (ct : CancellationToken) : Task<Feed> =
    task {
        use stream = XmlReader.Create(uri.ToString(), XmlReaderSettings(Async = true))
        return! XDocument.LoadAsync(stream, LoadOptions.None, ct)
    }

let private getItemTitle(item : XElement) =
    item.Element(XName.Get "title").Value

let filterFeed (itemTitle : Regex) (feed : Feed) : Feed =
    let newFeed = XDocument feed
    let items = newFeed.XPathSelectElements "/rss/channel/item"
    let itemsToRemove =
        items
        |> Seq.filter(getItemTitle >> (not << itemTitle.IsMatch))
    itemsToRemove
    |> ResizeArray
    |> Seq.iter(fun item -> item.Remove())
    newFeed

let serialize(feed : Feed) : string =
    feed.ToString(SaveOptions.DisableFormatting)
