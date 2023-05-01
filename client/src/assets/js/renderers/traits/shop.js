'use strict';
let positiveTraits;
let neutralTraits;
let negativeTraits;

async function initTraits()
{
    positiveTraits = await get(`traits?traitType=POSITIVE`)
        .then(res => res.json())
        .then(traits => {return traits;});
    neutralTraits = await get(`traits?traitType=NEUTRAL`)
        .then(res => res.json())
        .then(traits => {return traits;});
    negativeTraits = await get(`traits?traitType=NEGATIVE`)
        .then(res => res.json())
        .then(traits => {return traits;});
}

async function loadAllKindOfShopTraits()
{
    await initTraits();
    initShopSearchForms();
    const $positiveTraitList = document.querySelector("[data-shop-traitlist='pos']");
    const $neutralTraitList = document.querySelector("[data-shop-traitlist='neu']");
    const $negativeTraitList = document.querySelector("[data-shop-traitlist='neg']");

    const positiveOwnedTraits = await get(`martians/${getCurrentMarsId()}/traits?traitType=POSITIVE`)
        .then(res => res.json())
        .then(traits => {return traits;});
    const neutralOwnedTraits = await get(`martians/${getCurrentMarsId()}/traits?traitType=NEUTRAL`)
        .then(res => res.json())
        .then(traits => {return traits;});
    const negativeOwnedTraits = await get(`martians/${getCurrentMarsId()}/traits?traitType=NEGATIVE`)
        .then(res => res.json())
        .then(traits => {return traits;});

    const filteredPositiveShopTraits = filterShopTraits(positiveOwnedTraits, positiveTraits);
    const filteredNeutralShopTraits = filterShopTraits(neutralOwnedTraits, neutralTraits);
    const filteredNegativeShopTraits = filterShopTraits(negativeOwnedTraits, negativeTraits);

    insertTraitsOfCurrentProfile($positiveTraitList, $neutralTraitList, $negativeTraitList, filteredPositiveShopTraits, filteredNeutralShopTraits, filteredNegativeShopTraits);

    setTraitNumbers($positiveTraitList, $neutralTraitList, $negativeTraitList);

    initShopDescriptionView();
}

function setTraitNumbers($positiveTraitList, $neutralTraitList, $negativeTraitList) {
    const $posTotal = document.querySelector("#pos-trait-total-shop");
    $posTotal.textContent = $positiveTraitList.children.length.toString();
    const $neuTotal = document.querySelector("#neu-trait-total-shop");
    $neuTotal.textContent = $neutralTraitList.children.length.toString();
    const $negTotal = document.querySelector("#neg-trait-total-shop");
    $negTotal.textContent = $negativeTraitList.children.length.toString();
}

function filterShopTraits(ownedTraits, traitsForShop)
{
    ownedTraits.forEach(ownedTrait => {
        traitsForShop.splice(traitsForShop.findIndex(trait => trait.name === ownedTrait.name), 1);
    });
    return traitsForShop;
}

function initShopDescriptionView()
{
    const $ownedTraits = document.querySelectorAll("div.trait-shop-main ul>li div.trait-list li.trait-name");
    $ownedTraits.forEach(trait => {
        trait.addEventListener('click', () => showShopTraitDescription(trait.id));
    });
}

async function showShopTraitDescription(traitName)
{
    const traitInfo = await get(`trait/${traitName}`)
        .then(res => res.json())
        .then(trait => {return trait;});

    await displayPopupConfirm(`BUY "${traitName} (${traitInfo.type})"?`, `${traitInfo.description}`, "Buy", "No")
                            .then(answer => {
                                if (answer.action === "true")
                                {
                                    const newTraitBody = {
                                        name: traitName
                                    };
                                    post(`martians/${getCurrentMarsId()}/traits`, newTraitBody)
                                        .then(() => goToOwnedTraits())
                                        .then(() => setCurrentMartian(getCurrentMarsId()));
                                }
                            });
}

function initShopSearchForms()
{
    const $positiveSearchForm = document.querySelector("#search-in-positive-traits-shop");
    const $neutralSearchForm = document.querySelector("#search-in-neutral-traits-shop");
    const $negativeSearchForm = document.querySelector("#search-in-negative-traits-shop");

    const $searchForms = [$positiveSearchForm, $neutralSearchForm, $negativeSearchForm];
    $searchForms.forEach(form => {
        const $inputField = form.querySelector("input[type='text']");
        $inputField.addEventListener('input', (e) => handleShopSearch(e));
    });
}

async function handleShopSearch(event)
{
    const $inputField = event.target;
    const $traitListId = $inputField.id.split("-")[0];
    const $filterValue = $inputField.value;

    const traitsToFilter = await getShopTraitsToFilter($traitListId);


    const $traitList = document.querySelector(`[data-shop-traitlist='${$traitListId}']`);

    const filteredTraits = [];
    traitsToFilter.filter(trait => {
        if (trait.name.toLowerCase().indexOf($filterValue.toLowerCase()) > -1)
        {
            filteredTraits.push(trait);
        }
    });

    insertFilteredShopTraits(filteredTraits, $traitList);
}

async function getShopTraitsToFilter($traitListId) {
    let list;
    switch ($traitListId)
    {
        case "pos":
            list = positiveTraits;
            break;
        case "neu":
            list = neutralTraits;
            break;
        case "neg":
            list = negativeTraits;
            break;
        default:
            break;
    }

    return list;
}

function insertFilteredShopTraits(filteredTraits, $htmlTarget)
{
    $htmlTarget.innerHTML = "";
    filteredTraits.forEach(filteredTrait => {
        $htmlTarget.insertAdjacentHTML("beforeend", `<li id="${filteredTrait.name}" class="trait-name">${filteredTrait.name}</li>`);
    });
    initShopDescriptionView();
}

function goToOwnedTraits()
{
    loadAllKindOfOwnedTraits();
    changeVisibility(".traits-main");
    displayPopupAlert("SUCCESS", "The trait is added to your account.", "Ok");
}

