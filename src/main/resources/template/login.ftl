<!DOCTYPE html>

<html>
<head>
    <title>Login</title>
    <style type="text/css">
        .label {text-align: right}
        .error {color: red}
        body {
            border: 20px solid black;  }
        }
    </style>
</head>

<body>
<h2>Login</h2>
<form method="post">
    <table bgcolor="#1E90FF">
        <tr>
            <td class="label">
                Username
            </td>
            <td>
                <input type="text" name="username" value="${username}">
            </td>
            <td class="error">
            </td>
        </tr>

        <tr>
            <td class="label">
                Password
            </td>
            <td>
                <input type="password" name="password" value="">
            </td>
            <td class="error">
            ${login_error}

            </td>
        </tr>

    </table>
    <input type="submit" class="button" STYLE="color:black;">
</form>
</body>

</html>
