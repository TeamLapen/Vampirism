// @ts-check
// Note: type annotations allow type checking and IDEs autocompletion

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'Vampirism',
  tagline: 'Wiki',
  url: 'https://teamlapen.github.io',
  baseUrl: '/Vampirism/',
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.ico',
  organizationName: 'TeamLapen',
  projectName: 'Vampirism',
  deploymentBranch: 'gh-pages',
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
          editUrl: 'https://github.com/TeamLapen/Vampirism/tree/gh-pages/',
          versions: {
            current: {
              /** this value must be changed if a new version is released */
                label: '1.9',
            }
          }
        },
        blog: {
          showReadingTime: true,
          editUrl: 'https://github.com/TeamLapen/Vampirism/tree/gh-pages/',
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
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
            type: 'doc',
            docId: 'wiki/intro',
            position: 'left',
            label: 'Wiki',
          },
          {
            type: 'doc',
            docId: 'api/intro',
            position: 'left',
            label: 'API',
          },
          {
            type: 'doc',
            docId: 'data/intro',
            position: 'left',
            label: 'Data Packs',
          },
          {to: '/blog', label: 'Blog', position: 'left'},
          {
            type: 'docsVersionDropdown',
            position: 'right',
            dropdownItemsAfter: [
              { to: 'https://github.com/TeamLapen/Vampirism/wiki/%5B1.7.10%5D-Getting-Started', label: 'For MC 1.7.10' },
            ],
            dropdownActiveClassDisabled: false,
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
            title: 'Docs',
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
            title: 'Community',
            items: [
              {
                label: 'Discord',
                href: 'https://discord.gg/wuamm4P',
              },
              {
                label: 'Twitter',
                href: 'https://twitter.com/Maxanier',
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
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
      },
      metadata: [
        { name: 'keywords', content: 'minecraft, vampirism, forge, wiki' },
        { name: 'twitter:card', content: 'summary' }
      ],
      image: 'img/logo512x512.png',
    }),
};

module.exports = config;
