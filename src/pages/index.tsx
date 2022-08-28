import React from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';

import styles from './index.module.css';

type Stuff = {
    Svg: React.ComponentType<React.ComponentProps<'svg'>>;
}

const Stuff: Stuff = {
    Svg: require('@site/static/img/vampirism-title.svg').default,
}

function HomepageHeader({Svg}: Stuff) {
  const {siteConfig} = useDocusaurusContext();
  return (
      <div>
          <header className={clsx('hero hero--primary', styles.heroBanner)}>
              <div className="container">
                  <h1 className="hero__title">{siteConfig.title}</h1>
                  <p className="hero__subtitle">{siteConfig.tagline}</p>
                  <div className={styles.buttons}>
                      <Link
                          className="button button--secondary button--lg"
                          to="/docs/wiki/intro">
                          Get Started
                          ️</Link>
                  </div>
              </div>
          </header>
          <header className={clsx('hero hero--secondary', styles.heroBannerImage)}>
              <div className="container">
                  <Svg role="img" width="100%" height="100%"></Svg>
              </div>
          </header>
      </div>
  );
}

export default function Home(): JSX.Element {
  const {siteConfig} = useDocusaurusContext();
  return (
    <Layout
      title={`Wiki`}
      description="Minecraft Vampirism Wiki">
      <HomepageHeader {...Stuff}/>
    </Layout>
  );
}
