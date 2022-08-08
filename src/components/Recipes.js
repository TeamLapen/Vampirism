export function sumSource(sources) {
    let source = {};
    sources.flatMap(a =>  a).forEach(a => {
        let type = source[a['type']];
        if (type != null) {
            let name = type[a['title']];
            if (name != null) {
                a['recipes'].forEach(l => name.push(l))
            } else {
                type[a['title']] = [...a['recipes']];
            }
        } else {
            type = {};
            type[a['title']] = [...a['recipes']];
            source[a['type']] = type;
        }
    })
    let result = [];
    for (let key in source) {
        for (let title in source[key]) {
            let entry = {};
            entry["type"] = key;
            entry["title"] = title;
            entry["recipes"] = source[key][title];
            result.push(entry)
        }
    }
    return result;
}

export function craftingRecipe(source, title = "Crafting") {
    return craftingRecipes([source], title)
}
export function craftingRecipes(sources, title = "Crafting") {
    return [
        {
            type: 'recipe',
            title: title,
            recipes: sources
        },
    ];
}