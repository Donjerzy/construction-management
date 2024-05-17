<script>
    import EmployeeComponent from "../components/employee-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import Button from "../components/button.svelte";
    import Loader from "../components/loading-component.svelte";
    import { get } from "svelte/store";
    let contentTitle = "Profile";
    let loading = false;


    async function logout() {
        loading = true;
        await fetch('http://localhost:8080/api/v1/employee/auth/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                token: get(accessToken)
            })
        });
        loading = false;
        firstName.set("");
        accessToken.set("");
        loggedIn.set("false");
        window.location.replace('/');  
    }

</script>


<EmployeeComponent contentTitle={contentTitle}>
    <div class="mt-5">
        {#if loading}
            <Loader />
        {:else}
            <Button 
                height=10 width=32 label="Logout" fontSize="sm" padding="8px"
                on:click={logout} />
        {/if}
    </div>
</EmployeeComponent>