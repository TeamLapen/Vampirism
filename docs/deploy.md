# Deploying a new version

FactionApi and Vampirism are deployed together because deploying separately is a pain when using the same repository.

## What is deployed?

#### To Maven

- FactionApi
- FactionApi-api
- Vampirism
- Vampirism-api

#### To Curseforge

- FactionApi
- Vampirism

#### To Modrinth

- FactionApi
- Vampirism


## How to deploy

The mods can be deployed either as alpha, beta or release.

#### Deploy as alpha

Tag the commit with ``<mc-version>-<mod-version>-alpha.<alpha-identifier>``. E.g. ``26.1-2.0.0-alpha.1``.

#### Deploy as beta

Tag the commit with ``<mc-version>-<mod-version>-beta.<beta-identifier>``. E.g. ``26.1-2.0.0-beta.1``.

#### Deploy as release

Either:

1. Tag the commit with ``<mc-version>-<mod-version>``. E.g. ``26.1-2.0.0``.
2. Create a release on GitHub based on the tag and add the release notes.

Or:

1. Create a release on GitHub based on the branch with the tag ``<mc-version>-<mod-version>`` and add the release notes.