// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

import {themes as prismThemes} from 'prism-react-renderer';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Vampirism',
  tagline: 'Wiki',
  url: 'https://wiki.vampirism.dev',
  baseUrl: '/',
  onBrokenLinks: 'throw',
  favicon: 'img/favicon.ico',
  organizationName: 'TeamLapen',
  projectName: 'Vampirism',
  deploymentBranch: 'pages/stable',
  trailingSlash: false,

  // Even if you don't use internalization, you can use this field to set useful
  // metadata like html lang. For example, if your site is Chinese, you may want
  // to replace "en" with "zh-Hans".
  i18n: {
    defaultLocale: 'en',
    locales: ['en'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: 'https://github.com/TeamLapen/Vampirism/tree/pages/dev/',
          lastVersion: 'current',
          versions: {
            current: {
              /** this value must be changed if a new version is released */
              label: '2.x',
            }
          },
        },
        blog: {
          showReadingTime: true,
          editUrl: 'https://github.com/TeamLapen/Vampirism/tree/gh-pages/',
          blogTitle: 'Vampirism Blog',
          blogSidebarCount: 'ALL',
          blogSidebarTitle: 'All posts',
          onUntruncatedBlogPosts: 'ignore'
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  plugins: [
    [
      '@docusaurus/plugin-content-docs',
      {
        id: 'integrations',
        path: 'integrations',
        routeBasePath: 'integrations',
        sidebarPath: './sidebarsIntegrations.js',
        versions: {
          current: {
            label: '1.8',
          }
        }
      },
    ],
    [
      '@docusaurus/plugin-content-docs',
      {
        id: 'factionapi',
        path: 'factionapi',
        routeBasePath: 'factionapi',
        sidebarPath: './sidebarsFactionapi.js',
        versions: {
          current: {
            label: '1.x',
          }
        }
      },
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'Vampirism',
        logo: {
          alt: 'Vampirism Logo',
          src: 'img/fang.png',
        },
        items: [
          {
            type: 'dropdown',
            label: 'Vampirism',
            position: 'left',
            items: [
              {
                type: 'doc',
                docId: 'wiki/intro',
                label: 'Wiki',
              },
              {
                type: 'doc',
                docId: 'api/intro',
                label: 'API',
              },
              {
                type: 'doc',
                docId: 'data/intro',
                label: 'Data Packs',
              },
            ]
          },
          {
            type: 'dropdown',
            label: 'FactionApi',
            position: 'left',
            items: [
              {
                type: 'doc',
                docId: 'wiki/intro',
                docsPluginId: 'factionapi',
                label: 'Wiki',
              },
              {
                type: 'doc',
                docId: 'api/intro',
                docsPluginId: 'factionapi',
                label: 'API',
              },
              {
                type: 'doc',
                docId: 'data/intro',
                docsPluginId: 'factionapi',
                label: 'Data Packs',
              },
            ]
          },
          {
            type: 'doc',
            docId: 'wiki/intro',
            docsPluginId: 'integrations',
            position: 'left',
            label: 'Integrations',
          },
          {to: '/blog', label: 'Blog', position: 'left'},
          {
            href: 'https://vampirism.dev',
            label: 'Website',
            position: 'left',
          },
          {
            type: 'docsVersionDropdown',
            position: 'right',
            dropdownItemsAfter: [
              { to: 'https://github.com/TeamLapen/Vampirism/wiki', label: 'For MC 1.7.10' },
              { to: 'https://github.com/TeamLapen/Vampirism/wiki', label: 'For MC 1.12' },
            ],
            dropdownActiveClassDisabled: true,
          },
          {
            type: 'docsVersionDropdown',
            position: 'right',
            dropdownActiveClassDisabled: false,
            docsPluginId: 'factionapi',
          },
          {
            type: 'docsVersionDropdown',
            position: 'right',
            dropdownItemsAfter: [
              { to: 'https://github.com/TeamLapen/VampirismIntegrations/wiki', label: 'For MC 1.19 or older' },
            ],
            dropdownActiveClassDisabled: false,
            docsPluginId: 'integrations',
          },
          {
            href: 'https://github.com/Teamlapen/Vampirism',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Vampirism',
            items: [
              {
                label: 'Wiki',
                to: '/docs/wiki/intro',
              },
              {
                label: 'API',
                to: '/docs/api/intro',
              },
              {
                label: 'Data Pack',
                to: '/docs/data/intro',
              },
            ],
          },
          {
            title: 'FactionApi',
            items: [
              {
                label: 'Wiki',
                to: '/factionapi/wiki/intro',
              },
              {
                label: 'API',
                to: '/factionapi/api/intro',
              },
              {
                label: 'Data Pack',
                to: '/factionapi/data/intro',
              },
            ],
          },
          {
            title: 'Community',
            items: [
              {
                label: 'Discord',
                href: 'https://discord.gg/wuamm4P',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'Blog',
                to: '/blog',
              },
              {
                label: 'GitHub',
                href: 'https://github.com/Teamlapen/Vampirism',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} Vampirism, Contributors`,
      },
      prism: {
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
      },
      metadata: [
        { name: 'keywords', content: 'minecraft, vampirism, forge, wiki' },
        { name: 'twitter:card', content: 'summary' }
      ],
      image: 'img/fang.png',
    }),
};

export default config;