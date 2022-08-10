import React from "react";

export const baseUrl = 'https://raw.githubusercontent.com/TeamLapen/Vampirism/'
export const baseCommit = '4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a'
export const skillPath = '/src/main/resources/assets/vampirism/textures/skills/'
export const actionPath = '/src/main/resources/assets/vampirism/textures/actions/'


export const SkillImg = ({src, alt}) => (
    <img src={src} alt={alt} style={{height: 70, imageRendering: "pixelated"}}/>
);

export const UnlockAction = ({children}) => (
    <span style={{
        color: '#0e9c2f',
        fontStyle: 'italic'
    }}>
        {children}
    </span>
);

export const UpgradesSkill = ({children}) => (
    <span style={{
        color: '#10a8b3',
        fontStyle: 'italic'
    }}>
        {children}
    </span>
);

export const SkillOrAction = ({children, title, iconPath}) => (
    <div style={{ marginBottom: '15px'}} id={title.toLowerCase().replaceAll(' ','-')}>
        <div className="container" style={{ display: 'flex', alignItems: 'center', marginLeft: '-20px'}}>
            <div className="image">
                <SkillImg src={iconPath} alt={title}/>
            </div>
            <div className="text" style={{ marginLeft: '10px'}}>
                <h2>{title}</h2>
            </div>
        </div>
        <span >
            {children}
        </span>
    </div>
);

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

export const SkillBranch = ({children, title}) =>(
    <div>
        <h1>{title}</h1>
        <div style={{ marginLeft: '40px'}}>
            {children}
        </div>
    </div>
);