'use strict';

function loadTableData()
{
    const $positiveCollapsibleTraitList = document.querySelector("[data-collabsible-type='positive']");
    const $neutralCollapsibleTraitList = document.querySelector("[data-collabsible-type='neutral']");
    const $negativeCollapsibleTraitList = document.querySelector("[data-collabsible-type='negative']");
    getTableData($positiveCollapsibleTraitList, $neutralCollapsibleTraitList, $negativeCollapsibleTraitList);
}

async function getTableData($htmlPositiveTarget, $htmlNeutralTarget, $htmlNegativeTarget)
{
    const positiveTraits = await get(`martians/${getCurrentMarsId()}/traits?traitType=POSITIVE`)
        .then(res => res.json())
        .then(traits => {return traits;});

    const neutralTraits = await get(`martians/${getCurrentMarsId()}/traits?traitType=NEUTRAL`)
        .then(res => res.json())
        .then(traits => {return traits;});

    const negativeTraits = await get(`martians/${getCurrentMarsId()}/traits?traitType=NEGATIVE`)
        .then(res => res.json())
        .then(traits => {return traits;});

    insertTraitsOfCurrentProfile($htmlPositiveTarget, $htmlNeutralTarget, $htmlNegativeTarget, positiveTraits, neutralTraits, negativeTraits);
}

function insertTraitsOfCurrentProfile($htmlPositiveTarget, $htmlNeutralTarget, $htmlNegativeTarget, positiveTraits, neutralTraits, negativeTraits)
{
    insertIntoPositive($htmlPositiveTarget, positiveTraits);
    insertIntoNeutral($htmlNeutralTarget, neutralTraits);
    insertIntoNegative($htmlNegativeTarget, negativeTraits);
}

function insertIntoPositive($htmlTarget, traits)
{
    insertIntoTraitList($htmlTarget, traits);
}

function insertIntoNeutral($htmlTarget, traits)
{
    insertIntoTraitList($htmlTarget, traits);
}

function insertIntoNegative($htmlTarget, traits)
{
    insertIntoTraitList($htmlTarget, traits);
}

function insertIntoTraitList($htmlTarget, traits)
{
    $htmlTarget.innerHTML = "";
    if (traits.length !== 0)
    {
        traits.forEach(trait => {
            $htmlTarget.insertAdjacentHTML("beforeend", `<li id="${trait.name}" class="trait-name">${trait.name}</li>`);
        });
    }
    else
    {
        $htmlTarget.insertAdjacentHTML("beforeend", `<strong>No traits of this type</strong>`);
    }
}

