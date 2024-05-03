<script>
    export let projectId;
    export let employeeId;
    import AdminComponent from "../components/admin-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js'; 
    import { get } from "svelte/store";
    let appName = "Mjengo Bora Construction";
    let contentTitle = "Employee"

</script>


<AdminComponent appName={appName} contentTitle={contentTitle} userFirstName={get(firstName)}>
    <p>Hello</p>
</AdminComponent>