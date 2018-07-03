param (
    $HttpPort = 15003,
    [switch] $Detach
)

$ErrorActionPreference = 'Stop'

Push-Location $PSScriptRoot
try {
    $env:rssfilter_web_port = $HttpPort

    $arguments = @('--project-name', 'rssfilter', 'up', '--build', '--force-recreate')
    if ($Detach) {
        $arguments += '-d'
    }

    docker-compose $arguments
    if (-not $?) {
        throw "docker-compose returned error exit code"
    }
} finally {
    Pop-Location
}
