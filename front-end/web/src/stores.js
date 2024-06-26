import { writable } from "svelte/store";
import {browser} from "$app/environment";

export const loggedIn = writable( browser &&
    (localStorage.getItem("logged-in")|| "false")
);

loggedIn.subscribe((val) => browser && localStorage.setItem("logged-in", val));

export const accessToken = writable( browser &&
    (localStorage.getItem("access")|| "n/a")
);

accessToken.subscribe((val) => browser && localStorage.setItem("access", val));

export const projectUUID = writable( browser &&
    (localStorage.getItem("projectId")|| "n/a")
);

projectUUID.subscribe((val) => browser && localStorage.setItem("projectId", val));


export const firstName = writable( browser &&
    (localStorage.getItem("first-name")|| "n/a")
);

firstName.subscribe((val) => browser && localStorage.setItem("first-name", val));


export const projectClient = writable(
    {
        id: 0,
        name: "demo",
        type: "demo",
        committedAmount: 0.0,
        investedAmount: 0.0
    }
);
