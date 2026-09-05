// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

import {themes as prismThemes} from 'prism-react-renderer';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Vampirism',
  tagline: 'The official wiki for the Vampirism Minecraft mod — become a vampire or a vampire hunter.',
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

  stylesheets: [
    {
      href: 'https://fonts.googleapis.com/css2?family=Raleway:wght@400;500;600;700;800&family=Lora:ital,wght@0,400;0,700;1,400&display=swap',
      type: 'text/css',
    },
  ],

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

  themes: [
    [
      require.resolve('@easyops-cn/docusaurus-search-local'),
      /** @type {import('@easyops-cn/docusaurus-search-local').PluginOptions} */
      ({
        hashed: true,
        indexBlog: true,
        docsRouteBasePath: ['/docs', '/factionapi', '/integrations'],
        docsDir: ['docs', 'factionapi', 'integrations'],
        highlightSearchTermsOnTargetPage: true,
        searchResultLimits: 8,
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      colorMode: {
        respectPrefersColorScheme: true,
      },
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
              { to: 'https://github.com/TeamLapen/Vampirism/wiki', label: 'For MC 1.12 and older' },
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
            position: 'right',
            className: 'header-github-link',
            'aria-label': 'GitHub repository',
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
            title: 'Download',
            items: [
              {
                label: 'CurseForge',
                href: 'https://www.curseforge.com/minecraft/mc-mods/vampirism-become-a-vampire',
              },
              {
                label: 'Modrinth',
                href: 'https://modrinth.com/mod/vampirism',
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
              {
                label: 'Blog',
                to: '/blog',
              },
              {
                label: 'Website',
                href: 'https://vampirism.dev',
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
        { name: 'keywords', content: 'minecraft, vampirism, mod, wiki, vampire, hunter, neoforge, forge' },
        { name: 'description', content: 'Official wiki for the Vampirism Minecraft mod: gameplay guides, content reference, modding API and data pack documentation.' },
        { name: 'og:description', content: 'Official wiki for the Vampirism Minecraft mod: gameplay guides, content reference, modding API and data pack documentation.' },
        { name: 'twitter:card', content: 'summary_large_image' },
      ],
      image: 'img/social-card.jpg',
    }),
};

export default config;