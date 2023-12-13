import React from 'react';
import DocsVersionDropdownNavbarItem from '@theme-original/NavbarItem/DocsVersionDropdownNavbarItem';
import { useLocation }  from '@docusaurus/router';
import {
    useActivePluginAndVersion,
} from '@docusaurus/plugin-content-docs/client';

export default function DocsVersionDropdownNavbarItemWrapper(props) {
    const {docsPluginId, className, type} = props
    const {pathname} = useLocation()
    const doesPathnameContainDocsPluginId = pathname.includes(docsPluginId) || (pathname.includes('docs') && docsPluginId === undefined)
    if (!doesPathnameContainDocsPluginId) {
        return null
    }
    return (
        <>
            <DocsVersionDropdownNavbarItem {...props} />
        </>
    );
}
