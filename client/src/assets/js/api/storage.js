'use strict';
const KEY = "marsonalityProfile";
const TOKEN_KEY = "marsonalityToken";
const TOKEN_VALUE = "39d9c89d822364d803feab9ae0171ec7d5b20ef4d558b088aeaec7ecbdefe23e";
const PUBLIC_KEY_VALUE = "BEm8_ELP1kwa1OyBj_kQ22fjGQ3ZkJIjIFlD-Ton056lVo_jz-lIfGsuV3CvUi1UN1AXkfDPNLEqHvPp77a2s_c=";
const PUBLIC_KEY = "marsonalityPublicKey";

function setCurrentMartian(marsId)
{
    get(`martians/${marsId}`)
        .then(res => res.json())
        .then(martian =>  {
            if (martian.failure) {
                throw new Error("This martian does not exist");
            }
            saveToStorage(KEY, martian);
        })
        .catch(err => console.log(err));
}

function getCurrentMartian()
{
    if (loadFromStorage(KEY) === null) {
        return null;
    }
    return loadFromStorage(KEY);
}

function getCurrentMarsId()
{
    return getCurrentMartian().marsId;
}

function saveToStorage(key, value) {
    if (localStorage) {
        return localStorage.setItem(key,JSON.stringify(value));
    }
    return false;
}

function loadFromStorage(key) {
    if (localStorage) {
        return JSON.parse(localStorage.getItem(key));
    }
    return false;
}

function removeMartianFromStorage()
{
    if (localStorage.getItem(KEY) != null)
    {
        localStorage.removeItem(KEY);
    }
}

function setOpenApiToken()
{
    saveToStorage(TOKEN_KEY, TOKEN_VALUE);
    saveToStorage(PUBLIC_KEY, PUBLIC_KEY_VALUE);
}

function getPushPublicKey()
{
    if (localStorage) {
        return JSON.parse(localStorage.getItem(PUBLIC_KEY));
    }
    return "";
}

function getOpenApiToken()
{
    if (localStorage) {
        return JSON.parse(localStorage.getItem(TOKEN_KEY));
    }
    return "";
}
