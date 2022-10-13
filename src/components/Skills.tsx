import React from "react";

export const SkillImg = ({src, alt}) => (
    <img src={src} alt={alt} style={{height: 70, imageRendering: "pixelated"}}/>
);

export const UnlockAction = ({action}) => (
    <span style={{
        color: '#0e9c2f',
        fontStyle: 'italic'
    }}>
        Unlocks {action} Action
    </span>
);

export const UpgradesSkill = ({skill}) => (
    <span style={{
        color: '#10a8b3',
        fontStyle: 'italic'
    }}>
        Upgrades Skill: {skill}
    </span>
);

export const UnlocksVision = ({vision}) => (
    <span style={{
        color: '#b31072',
        fontStyle: 'italic'
    }}>
        Unlocks Vision: {vision}
    </span>
);

export const SkillOrAction = ({children, title, iconPath}) => (
    <div style={{ marginTop: '15px'}} id={title.toLowerCase().replaceAll(' ','-')}>
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

export const SkillBranch = ({children, title}) =>(
    <div>
        <h1>{title}</h1>
        <div style={{ marginLeft: '40px'}}>
            {children}
        </div>
    </div>
);