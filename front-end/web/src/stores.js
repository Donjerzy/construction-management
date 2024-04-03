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