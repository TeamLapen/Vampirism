import React from "react";
import {SkillOrAction} from "../../_components/Skills";

const resolve = (icon) => {
    const m = require(`./icons/${icon}`);
    return m.default ?? m;
};

export function VSkill({children, title, icon}) {
    return (<SkillOrAction title={title} iconPath={resolve(icon)}>
        {children}
    </SkillOrAction>);
}

export const VAction = VSkill;
