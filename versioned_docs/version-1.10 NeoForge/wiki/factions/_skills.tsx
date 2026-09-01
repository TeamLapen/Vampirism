import React from "react";
import {SkillOrAction} from "@site/src/components/Skills";

export const baseUrl = 'https://raw.githubusercontent.com/TeamLapen/Vampirism/'
export const baseCommit = '1c387d7c7553fe70d789d1f9f34530d745e330b0'
export const skillPath = '/src/main/resources/assets/vampirism/textures/skills/'
export const actionPath = '/src/main/resources/assets/vampirism/textures/actions/'

export function Skill({children, title, icon, commit=baseCommit}) {
    return (<SkillOrAction title={title} iconPath={baseUrl.concat(commit).concat(skillPath).concat(icon)}>
        {children}
    </SkillOrAction>);
}

export function Action({children, title, icon, commit=baseCommit}) {
    return(<SkillOrAction title={title} iconPath={baseUrl.concat(commit).concat(actionPath).concat(icon)}>
        {children}
    </SkillOrAction>);
}