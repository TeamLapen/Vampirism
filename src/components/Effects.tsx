import React from "react";

export const baseUrl = 'https://raw.githubusercontent.com/TeamLapen/Vampirism/'
export const baseCommit = '4bf2c73fb860a23de225edbae9c0b1c1ead3dd1a'
export const effectPath = '/src/main/resources/assets/vampirism/textures/mob_effect/'

export const EffectImg = ({src, alt}) => (
    <img src={src} alt={alt} style={{height: 70, imageRendering: "pixelated"}}/>
);

const EffectInternal = ({children, title, icon}) => (
    <div style={{ marginBottom: '15px'}} id={title.toLowerCase().replaceAll(' ','-')}>
        <div className="container" style={{ display: 'flex', alignItems: 'center', marginLeft: '-20px'}}>
            <div className="image">
                <EffectImg src={icon} alt={title}/>
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

export const Effect = ({children, title, icon, commit=baseCommit}) => (
    <EffectInternal title={title} icon={baseUrl.concat(commit).concat(effectPath).concat(icon)} children={children}/>
);

export const EffectC = ({children, title, icon}) => (
    <EffectInternal title={title} children={children} icon={icon}/>
);