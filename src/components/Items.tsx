import React from "react";

interface Image {
    src: string;
    alt: string;
}
interface ItemImgProps {
    src: string;
    alt: string;
    imageHeight: string | number;
}
export const ItemImg = ({src, alt, imageHeight = 200} : ItemImgProps) => (
    <img src={src} alt={alt} style={{height: (typeof imageHeight === 'number' ? `${imageHeight}px` : imageHeight) as string, imageRendering: "pixelated"}}/>
);

interface ItemsImgProps {
    children: any;
    title: any;
    img: Image[];
    source: any;
    subHeading?: boolean;
    imageHeight?: string | number;
}
export const Items = ({children, title, img, source, subHeading = false, imageHeight = 100} : ItemsImgProps) => (
    <div style={{marginBottom: '50px'}} id={title.toLowerCase().replaceAll(' ', '-')}>
        <div>
            {!subHeading && <h1 style={{fontSize: 'xx-large'}}>{title}</h1>}
            {subHeading && <h2 style={{fontSize: 'x-large'}}>{title}</h2>}
        </div>
        <div className="image" style={{marginLeft: '10px', marginBottom: '20px'}}>
            {img.map(imagePair => (
                <ItemImg src={imagePair.src} alt={imagePair.alt} imageHeight={imageHeight}/>
            ))}
        </div>
        <span>
            {children}
        </span>
        <div>
        {source != null && <div style={{marginTop: '20px'}}>
            <details>
                <summary>How to Acquire</summary>
                {source.map(item => {
                    switch (item.type) {
                        case "list":
                            return (
                                <span>
                                    <h4>{item.title}</h4>
                                    <ul>
                                        {item.source.map(source => (
                                            <li>{source}</li>
                                        ))}
                                    </ul>
                                </span>
                            );
                        case "recipe":
                            return (<span>
                               <h4>{item.title}</h4>
                                {item.source.map(recipe => (
                                    <img src={recipe} alt="" style={{width: 500, imageRendering: "pixelated"}}/>
                                ))}
                           </span>);
                    }
                })}
            </details>
        </div>}
        </div>
    </div>
);

export const Item = ({children, title, img, source, imageHeight = 100}) => (
    <Items children={children} title={title} source={source} img={[{src: img, alt: title}]} imageHeight={imageHeight}></Items>
);

export const TieredItem = ({children, title, tiers, imageHeight = 100}) => (
    <div style={{marginBottom: '50px'}} id={title.toLowerCase().replaceAll(' ', '-')}>
        <div>
            <h1 style={{fontSize: 'xx-large'}}>{title}</h1>
        </div>
        {children}
        {tiers.map(tier => (
            <div>
                <Items children={tier.children} title={tier.title} source={tier.source} img={tier.img} subHeading={true} imageHeight={imageHeight}></Items>
            </div>))}
    </div>
);
