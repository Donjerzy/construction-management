<script>
    import { get } from "svelte/store";
    import AdminComponent from "../components/admin-component.svelte";
    import {loggedIn, accessToken, firstName} from "../stores.js";

    let pageTitle = 'Setups';
    let appName = 'Mjengo Bora Construction'
</script>


<AdminComponent appName={appName} contentTitle={pageTitle} userFirstName={get(firstName)}>

</AdminComponent>