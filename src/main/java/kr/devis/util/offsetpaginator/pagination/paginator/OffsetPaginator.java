package kr.devis.util.offsetpaginator.pagination.paginator;

import kr.devis.util.offsetpaginator.pagination.constant.PaginatorConstant;
import kr.devis.util.offsetpaginator.pagination.constant.PagingOption;

/**
 * <b>Since 11. 2021 by Kimchi-dev</b>
 * <hr>
 * Implementation Proxy of Paginator
 * <p>This Proxy just supply paginator with DefaultPaginator</p>
 */
public class OffsetPaginator implements Paginator {

    private final AbstractDefaultPaginator paginator;

    public OffsetPaginator() {
        paginator = new DefaultPaginator();
    }

    public OffsetPaginator(int totalContentsCount, int currentPage) throws Exception{
        paginator = new DefaultPaginator(totalContentsCount, currentPage);
    }

    @Override
    public PagingOption getPagingOption() throws Exception {
        return paginator.getPagingOption();
    }

    /**
     *
     * @param totalContentsCount Number of total contents - Assign Your board list.size()
     * @param numPerPage Number of page-number per page - [1][2][3][4][5]
     * @param contentsPerPage  Number of contents(board) per page;
     * @param currentPage currently page number
     * @param constant Select Your Database. default PaginatorConstant.MYSQL_PAGING
     * @throws Exception throw exceptions according to initialize
     */
    @Override
    public void init(int totalContentsCount, int numPerPage, int contentsPerPage, int currentPage, PaginatorConstant constant) throws Exception {
        paginator.init(totalContentsCount, numPerPage, contentsPerPage, currentPage, constant);
    }

    @Override
    public Paginator elastic() throws Exception {
        return paginator.elastic();
    }

    @Override
    public Paginator fixed() throws Exception {
        return paginator.fixed();
    }


    public Paginator move(boolean isPre, boolean isNext) throws Exception {
        return paginator.move(isPre, isNext);
    }

    @Override
    public ResultPaginator build()throws Exception {
        return paginator.build();
    }

    @Override
    public String getPagingLog() {
        return paginator.getPagingLog();
    }

    @Override
    public double getResultTime() {
        return paginator.getResultTime();
    }
}
