const priceParser = (value) => {
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

export {
    priceParser
}