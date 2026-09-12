import React from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import useBaseUrl from '@docusaurus/useBaseUrl';
import Layout from '@theme/Layout';

import styles from './index.module.css';

const TitleSvg = require('@site/static/img/vampirism-title.svg').default;

type Section = {
  title: string;
  description: string;
  to: string;
  cta: string;
};

const SECTIONS: Section[] = [
  {
    title: 'Wiki',
    description:
      'Learn the mod: how to become a vampire or hunter, leveling, skills, factions and lords.',
    to: '/docs/wiki/intro',
    cta: 'Read the guide',
  },
  {
    title: 'Content Reference',
    description:
      'Every block, item, oil, potion, effect, entity and structure Vampirism adds, with recipes.',
    to: '/docs/1.10 NeoForge/wiki/content/items',
    cta: 'Browse content',
  },
  {
    title: 'Modding API',
    description:
      'Depend on Vampirism from your own mod: registries, player capabilities, events and IMC.',
    to: '/docs/api/intro',
    cta: 'Open the API docs',
  },
  {
    title: 'Data Packs',
    description:
      'Tweak blood values, convertibles, sun damage, recipes and loot without writing code.',
    to: '/docs/data/intro',
    cta: 'Customize with data',
  },
  {
    title: 'FactionApi',
    description:
      'The faction, skill, level and task framework Vampirism is built on — usable on its own.',
    to: '/factionapi/wiki/intro',
    cta: 'Explore FactionApi',
  },
  {
    title: 'Integrations',
    description:
      'How Vampirism works together with other mods such as Jade and WAILA.',
    to: '/integrations/wiki/intro',
    cta: 'See integrations',
  },
];

function HomepageHeader() {
  const {siteConfig} = useDocusaurusContext();
  const heroImage = useBaseUrl('/img/hero.png');
  return (
    <header
      className={clsx('hero', styles.heroBanner)}
      style={{backgroundImage: `url(${heroImage})`}}>
      <div className={clsx('container', styles.heroInner)}>
        <TitleSvg
          className={styles.heroTitle}
          role="img"
          aria-label={`${siteConfig.title} logo`}
        />
        <h1 className={styles.visuallyHidden}>{siteConfig.title} Wiki</h1>
        <p className={styles.heroSubtitle}>
          Become a vampire or a vampire hunter. Everything you need to play,
          build data packs for, or write mods against Vampirism.
        </p>
        <div className={styles.heroButtons}>
          <Link className="button button--primary button--lg" to="/docs/wiki/intro">
            Get started
          </Link>
          <Link
            className="button button--secondary button--lg"
            to="/factionapi/wiki/intro">
            FactionApi
          </Link>
        </div>
      </div>
    </header>
  );
}

function SectionCards() {
  return (
    <section className={styles.sections}>
      <div className="container">
        <div className={styles.sectionGrid}>
          {SECTIONS.map((section) => (
            <Link key={section.to} to={section.to} className={styles.card}>
              <h2 className={styles.cardTitle}>{section.title}</h2>
              <p className={styles.cardDescription}>{section.description}</p>
              <span className={styles.cardCta}>{section.cta} →</span>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
}

export default function Home(): JSX.Element {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title="Wiki"
      description={siteConfig.tagline}>
      <HomepageHeader />
      <main>
        <SectionCards />
      </main>
    </Layout>
  );
}
