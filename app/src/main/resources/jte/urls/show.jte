@import hexlet.code.dto.urls.UrlPage
@import hexlet.code.utils.NamedRoutes

@param UrlPage page

@template.layout.page(
page = page,
content = @`
    <div class="container-lg mt-5">

        <h1>Сайт ${page.getUrl().getName()}</h1>

        <table class="table table-bordered table-hover mt-3">
            <tbody>
            <tr>
                <td>ID</td>
                <td>${page.getUrl().getId()}</td>
            </tr>
            <tr>
                <td>Имя</td>
                <td>${page.getUrl().getName()}</td>
            </tr>
            <tr>
                <td>Дата создания</td>
                <td>${String.valueOf(page.getUrl().getCreatedAt()).substring(0,16)}</td>
            </tr>
            </tbody>
        </table>

        <h2 class="mt-5">Проверки</h2>
        <form method="post" action="/urls/${page.getUrl().getId()}/checks">
            <button type="submit" class="btn btn-primary">Запустить проверку</button>
        </form>

        <table class="table table-bordered table-hover mt-3">
            <thead>
            <th class="col-1">ID</th>
            <th class="col-1">Код ответа</th>
            <th>title</th>
            <th>h1</th>
            <th>description</th>
            <th class="col-2">Дата проверки</th>
            </thead>
            <tbody>
            @if(page.getUrlChecks() != null)
                @for(var urlCheck : page.getUrlChecks())
                    <tr>
                        <td>${urlCheck.getId()}</td>
                        <td>${urlCheck.getStatusCode()}</td>
                        <td>${urlCheck.getTitle()}</td>
                        <td>${urlCheck.getH1()}</td>
                        <td>${urlCheck.getDescription()}</td>
                        <td>${String.valueOf(urlCheck.getCreatedAt()).substring(0,16)}</td>
                    </tr>
                @endfor
            @endif
            </tbody>
        </table>

        <nav aria-label="Page navigation">
            <ul class="pagination justify-content-center mt-5">
                <li class="page-item ${page.getConditionBack()}">
                    <a class="page-link" href="?page=${page.getPageNumber() < 2 ? 1 : page.getPageNumber() - 1}"
                       aria-label="Previous">
                        <span aria-hidden="true">&laquo;</span>
                    </a>
                </li>
                <li class="page-item active"><a class="page-link"
                                                href="${NamedRoutes.urlPath(page.getUrl().getId())}?page=${page.getPageNumber()}">${page.getPageNumber()}</a>
                </li>
                <li class="page-item ${page.getConditionNext()}">
                    <a class="page-link" href="?page=${page.getPageNumber() + 1}" aria-label="Next">
                        <span aria-hidden="true">&raquo;</span>
                    </a>
                </li>
            </ul>
        </nav>
    </div>
`
)
