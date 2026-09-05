import React from "react";
import useBrokenLinks from "@docusaurus/useBrokenLinks";

const EffectImg = ({src, alt}) => (
    <img src={src} alt={alt} style={{height: 70, imageRendering: "pixelated"}}/>
);

export const EffectInternal = ({children, title, icon}) => {
    const id = title.toLowerCase().replaceAll(' ','-');
    useBrokenLinks().collectAnchor(id);
    return (
        <div style={{ marginBottom: '15px'}} id={id}>
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
};