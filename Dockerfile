FROM microsoft/dotnet:2.1-sdk AS build-env
WORKDIR /app

# Copy csproj and restore as distinct layers
COPY ./src/RssFilter/RssFilter.fsproj ./src/RssFilter/
COPY ./RssFilter.sln ./
RUN dotnet restore

# Copy everything else and build
COPY . ./
RUN dotnet publish -c Release -o out

# Build runtime image
FROM microsoft/dotnet:2.1-aspnetcore-runtime
WORKDIR /app
COPY --from=build-env /app/src/RssFilter/out .

# Copy configuration
COPY ./src/RssFilter/appsettings.json ./

ENTRYPOINT ["dotnet", "RssFilter.dll"]
