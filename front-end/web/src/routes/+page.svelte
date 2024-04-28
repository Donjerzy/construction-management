<script>
    import Home from "../pages/Home.svelte";
    import Dashboard from "../pages/Dashboard.svelte"
    import {loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    
    let appName = 'Mjengo Bora';
    let userStatus = 'false';
    let value = get(loggedIn);
 
</script>

<svelte:head>
    <title>Home</title>
</svelte:head>


{#if value === 'false'}
<Home appName={appName} />
{:else}
<Dashboard />
{/if}





