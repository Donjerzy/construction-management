<script>
    let appName = 'Mjengo Bora'
</script>

<svelte:head>
    <title>Home</title>
</svelte:head>

<div class="main">
    <div id="header">
        <h1 class="app-name">{appName} Construction App</h1>
        <p id="sign-up">Sign Up</p>
    </div>
    <div class="form">
        <div id="title">
            <h2>Login</h2>
        </div>
        <form>
            <label for="email">Email</label>
            <input type="email" name="email" id="email">
            <label for="pass">Password</label>
            <input type="password" name="pass" id="pass">
            <p id="forgot">Forgot Password?</p>
            <button type="submit" id="login-btn">Log in</button>
        </form>
    </div>
</div>


<style>
    * {
        margin: 0;
        border: 0;
        /* box-sizing: border-box; */
    }
    #header {
        display: flex;
        justify-content: space-between;
    }
    #sign-up {
        font-size: 1.4rem;
    }
    #sign-up:hover {
        color: #38aa3b;
        cursor: pointer;
    }

    .main {
        position: relative;
        margin-left: 12%;
        margin-right: 12%;
        max-width: 100vw;
        min-height: 90vh;
        max-height: 90vh;
        padding: 20px;
        /* border: solid black 1px; */
    }
    .app-name {
        font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
        font-size: 1.5rem;
    }
    .app-name:hover {
        cursor: pointer;
    }
    .form {
        height: 80vh;
        width: 100%;
        /* border: solid black 1px; */
        /* margin-top: 20px; */
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
    }
    #title {
        margin-bottom: 20px;
        font-size: 1.4rem;
    }
    input {
        display: block;
        border: 1px solid;
        margin-bottom: 20px;
        height: 20px;
        width: 184px;
        padding: 4px;
    }
    #login-btn {
        padding: 12px;
        border-radius: 4px;
        background-color: #5c595c;
        width: 200px;
        border-radius: 20px;
        color:#ffffff;
    }
    #login-btn:hover {
        cursor: pointer;
        background-color: #38aa3b;
    }
    #forgot {
        margin-bottom: 12px;
        font-family: 'Times New Roman', Times, serif;
        color: #434143;
        font-size: 1rem;
    }
    #forgot:hover {
        cursor: pointer;
        color: #38aa3b;
    }
    h2 {
        font-family: Georgia, 'Times New Roman', Times, serif;
    }
    label {
        font-family: 'Times New Roman', Times, serif;
        font-size: 1rem;
    }
    
</style>

