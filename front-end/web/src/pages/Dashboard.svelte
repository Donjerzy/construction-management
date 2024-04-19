<script>
    import {firstName} from '../stores.js' 
    import { get } from "svelte/store";
    let userFirstName = get(firstName);
</script>



<svelte:head>
    <title>Dashboard</title>
</svelte:head>


<div class="container">
    <h1>Hello {userFirstName}</h1>
</div>



<style>
    :global(body) {
        margin: 0; 
        padding: 0; 
        box-sizing: border-box;
        max-width: 100vw;
        min-height: 100vh;
    }
    * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
    }
    .container {
        min-height: 100vh;
        width: 100%;
    }
</style>