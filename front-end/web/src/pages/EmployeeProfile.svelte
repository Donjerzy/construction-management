<script>
    import EmployeeComponent from "../components/employee-component.svelte";
    import {firstName, accessToken, loggedIn} from '../stores.js' 
    import Button from "../components/button.svelte";
    let contentTitle = "Profile";


    async function logout() {
        
    }


</script>


<EmployeeComponent contentTitle={contentTitle}>
    <div class="mt-5">
        <Button 
            height=10 width=32 label="Logout" fontSize="sm" padding="8px"
            on:click={logout} />
    </div>
</EmployeeComponent>