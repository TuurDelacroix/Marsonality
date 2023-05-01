"use strict";

const contentType = 'application/json;';

function get(uri) {
    const request = new Request(api + uri, {
        method: 'GET',
    });
    return call(request);
}

function post(uri, body) {
    const request = new Request(api + uri, {
        method: 'POST',
        headers: {
            'Content-type': contentType,
            'Authorization': `Bearer ${getOpenApiToken()}`
        },
        body: JSON.stringify(body)
    });

    return call(request);
}

function put(uri, body) {
    const request = new Request(api + uri, {
        method: 'PUT',
        headers: {
            'Content-type': contentType,
            'Authorization': `Bearer ${getOpenApiToken()}`
        },
        body: JSON.stringify(body)
    });

    return call(request);
}

function remove(uri, body) {
    const request = new Request(api + uri, {
        method: 'DELETE',
        headers: {
            'Content-type': contentType,
            'Authorization': `Bearer ${getOpenApiToken()}`
        },
        body: JSON.stringify(body)
    });

    return call(request);
}

function logJson(response) {
    response.json().then(console.log);
}

function logError(error) {
    console.error(error);
}

function call(request) {
    return fetch(request);
}
