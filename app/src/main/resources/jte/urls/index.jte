@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.utils.NamedRoutes

@import java.time.format.DateTimeFormatter
@param UrlsPage page

!{var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");}

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
                !{var urlCheck = page.getLastCheck().get(url.getId());}

                <tr>
                    <td>
                        ${url.getId()}
                    </td>
                    <td>
                        <a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a>
                    </td>
                    <td>
                        ${urlCheck == null ? "" : urlCheck.getCreatedAt().toLocalDateTime().format(formatter)}
                    </td>
                    <td>
                        ${urlCheck == null ? "" : String.valueOf(urlCheck.getStatusCode())}
                    </td>

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
