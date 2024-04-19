<script>
    import {firstName} from '../stores.js' 
    import { get } from "svelte/store";
    let userFirstName = get(firstName);
</script>



<h1>Hello {userFirstName}</h1>