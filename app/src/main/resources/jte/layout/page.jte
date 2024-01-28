@import gg.jte.Content
@import hexlet.code.utils.NamedRoutes
@import hexlet.code.repository.BaseRepository

@param Content content
@param BaseRepository page = null
@param Content header = null

<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
          crossorigin="anonymous">
    <title>project_72</title>
</head>
<body>
<div>
    <nav class="navbar navbar-expand-lg bg-body-tertiary" data-bs-theme="dark">
        <div class="container-fluid">
            <a class="navbar-brand" href="${NamedRoutes.rootPath()}">Анализатор страниц</a>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav">
                    <li class="nav-item">
                        <a class="nav-link" aria-current="page" href="#">Главная</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" aria-current="page" href="#">Сайты</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
    <div>
        ${header}
        ${content}
    </div>
    <div>
        <nav class="navbar fixed-bottom bg-body-tertiary" data-bs-theme="dark">
            <div class="container-fluid d-flex justify-content-center">
                <p class="fw-lighter text-body-emphasis">
                    featured by
                    <a href="https://github.com/paulvino">Pavel Vinogradov</a>
                    with
                    <a href="https://ru.hexlet.io">Hexlet</a>
                </p>
            </div>
        </nav>
    </div>
</div>
</body>
</html>