<script>
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import { get } from "svelte/store";
    import Button from "../components/button.svelte";
    let appName = "Mjengo Bora Construction";
    let contentTitle = "Profile";
    

    function logOutUser() {
        firstName.set("");
        accessToken.set("");
        loggedIn.set("false");
        window.location.replace('/'); 
    }


</script>

<svelte:head>
    <title>Profile</title>
</svelte:head>



<AdminComponent appName={appName} contentTitle={contentTitle} userFirstName={get(firstName)}>
    <div class="mt-2">
        <div class="mt-8">
            <Button 
                height=12 width=36 label="Logout" fontSize="sm" padding="8px"
                on:click={logOutUser} />
        </div>
    </div>
</AdminComponent>


