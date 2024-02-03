@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.utils.NamedRoutes

@param UrlsPage page


@template.layout.page(
page = page,
content = @`
    <div class="container-lg mt-5">

        <h1>Сайты</h1>

        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th class="col-1">ID</th>
                <th>Имя</th>
                <th class="col-2">Последняя проверка</th>
                <th class="col-1">Код ответа</th>
            </tr>
            </thead>
            <tbody>
            @for(var url : page.getUrls())
                <tr>
                    <td>
                        ${url.getId()}
                    </td>
                    <td>
                        <a href="/urls/${url.getId()}">${url.getName()}</a>
                    </td>
                    @if((page.getLastCheck().stream().anyMatch(check -> check.getUrlId() != null && check.getUrlId() == url.getId())))
                        <td>
                            ${page.getLastCheck().stream().filter(check -> check.getUrlId() == url.getId()).findFirst().get().getCreatedAt().toString().substring(0,16)}
                        </td>
                        <td>
                            ${page.getLastCheck().stream().filter(check -> check.getUrlId() == url.getId()).findFirst().get().getStatusCode()}
                        </td>
                    @else
                        <td>
                        </td>
                        <td>
                        </td>
                    @endif
                </tr>
            @endfor
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
                                                href="${NamedRoutes.urlsPath()}?page=${page.getPageNumber()}">${page.getPageNumber()}</a>
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
