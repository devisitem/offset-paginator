package pagination.paginator;

import pagination.calculator.Calculator;
import pagination.constant.PagingOption;
import pagination.constant.CalculateConstant;
import pagination.constant.PaginatorConstant;
import pagination.object.DefaultPaginationObject;
import pagination.object.PaginatedObject;
import pagination.object.PaginationObject;
import pagination.calculator.CalculatorProxy;

import java.util.Map;

public class DefaultPaginator extends AbstractDefaultPaginator {

    private Calculator calculator;
    private PaginationObject object;
    private PagingOption option;
    private PaginatorConstant constant;
    private ResultPaginator resultPaginator;

    @Override
    public PagingOption getPagingOption() {
        return this.option;
    }

    @Override
    protected void setPagingObject(PaginationObject object) {
        this.object = object;
    }

    @Override
    protected void validateAndInitializeConstant(PaginatorConstant constant) throws Throwable {
        boolean isSameConstant = super.getCurrentConstant().isSameConstant(constant);
        this.constant = constant;
        if( ! isSameConstant) {
            super.setConstant(constant);
        }
        this.calculator = new CalculatorProxy(constant);
    }

    @Override
    public void init(int totalContentsCount, int numPerPage, int contentsPerPage, int currentPage, PaginatorConstant constant) throws Throwable {

        PaginationObject object = new DefaultPaginationObject();
        object.initialize(totalContentsCount, numPerPage, contentsPerPage, currentPage);

        super.setPagingObject(object, constant);
    }

    @Override
    public Paginator elastic() throws Throwable {
        this.option = PagingOption.ELASTIC;
        this.calculator.applyOption(this.option);
        this.calculator.setting(object, false, false);
        return this;
    }

    @Override
    public Paginator fixed() throws Throwable {
        this.option = PagingOption.FIXED;
        this.calculator.applyOption(this.option);
        this.calculator.setting(object,false,false);
        return this;
    }

    @Override
    public Paginator pre() throws Throwable {
        if(this.option == null) {
            throw new NullPointerException("Paging option cannot be null. You have to choice paging mode. ex) paginator.elastic() or paginator.fixed()");
        }
        this.calculator.movePre(true);
        return this;
    }

    @Override
    public Paginator next() throws Throwable {
        if(this.option == null) {
            throw new NullPointerException("Paging option cannot be null. You have to choice paging mode. ex) paginator.elastic() or paginator.fixed()");
        }
        this.calculator.moveNext(true);
        return this;
    }

    public ResultPaginator build() throws Throwable {
        if(this.option == null) {
            throw new NullPointerException("Paging option cannot be null. You can this paginator with call like that -> paginator.elastic().build().paginate()");
        }
        long start = System.currentTimeMillis();

        this.calculator.calculate(this.object);
        PaginatedObject paginated = this.calculator.getResult();
        this.resultPaginator = new DefaultResultPaginator(paginated);

        long end = System.currentTimeMillis();
        super.setResultTime(((end - start) / 1000.0));

        return this.resultPaginator;
    }

    @Override
    public String getPagingLog() {
        StringBuilder builder = new StringBuilder();
        final String NEW_LINE = "\n";

        try {
            Map<String, Integer> calculated = this.calculator.getCalculatedData();
            Integer currentPage = calculated.get(CalculateConstant.CURRENT_PAGE.getValueName());
            builder
                    .append(NEW_LINE)
                    .append("----------- Welcome To Kimchi Paginator -----------").append(NEW_LINE);
            int startIndex = builder.length();
            if(this.object.isAbleToPreviousStep()) {
                builder.append("[Pre]");
            }
            int found = 0;
            for(int i = this.object.getStartPage();i <= this.object.getEndPage();i++){
                if(currentPage == i) {
                    found = builder.length();
                    builder.append(" "+i+" ");
                } else {
                    builder.append("["+i+"]");
                }
            }
            if(this.object.isAbleToNextStep()) {
                builder.append("[Next]");
            }

            int target = (found - startIndex);
            builder.append(NEW_LINE);

            for(int i = 0;i < (target + 1);i++) {
                builder.append(" ");
            }
            builder.append("↑");
            builder.append(NEW_LINE).append(NEW_LINE);
            builder
                    .append(String.format("[Total Page Count]      : [%5d]",calculated.get(CalculateConstant.TOTAL_PAGE_COUNT.getValueName()))).append(NEW_LINE)
                    .append(String.format("[Total Step Count]      : [%5d]",calculated.get(CalculateConstant.TOTAL_STEP_CNT.getValueName()))).append(NEW_LINE)
                    .append(String.format("[Currently Step]        : [%5d]",calculated.get(CalculateConstant.CURRENT_STEP.getValueName()))).append(NEW_LINE)
                    .append(String.format("[Currently Page Number] : [%5d]",currentPage)).append(NEW_LINE)
                    .append(String.format("[Result time] : [%.3f] sec", super.getResultTime())).append(NEW_LINE);

            if(this.constant.isSameConstant(PaginatorConstant.ORACLE_PAGING)) {
                builder
                        .append("You can paginate with Oracle DataBase's SQL that like following Query.").append(NEW_LINE)
                        .append("==> ").append(String.format("BETWEEN ROWNUM %5d AND %5d",calculated.get(CalculateConstant.START_INDEX.getValueName()), calculated.get(CalculateConstant.END_INDEX.getValueName()))).append(NEW_LINE);
            } else if (this.constant.isSameConstant(PaginatorConstant.MYSQL_PAGING)) {
                builder
                        .append("You can paginate with MySQL DataBase's SQL that like following Query.").append(NEW_LINE)
                        .append("==> ").append(String.format("LIMIT %5d, %5d", calculated.get(CalculateConstant.START_INDEX.getValueName()), calculated.get(CalculateConstant.END_INDEX.getValueName()))).append(NEW_LINE);
            } else if(this.constant.isSameConstant(PaginatorConstant.POSTGRESQL_PAGING)) {
                builder
                        .append("You can paginate with PostgreSQL DataBase's SQL that s like either following Queries.").append(NEW_LINE)
                        .append(" ==> ").append(String.format("LIMIT %5d OFFSET %5d", calculated.get(CalculateConstant.START_INDEX.getValueName()), calculated.get(CalculateConstant.END_INDEX.getValueName()))).append(NEW_LINE)
                        .append(" ==> ").append(String.format("OFFSET %5d ROWS FETCH LIMIT %5d", calculated.get(CalculateConstant.END_INDEX.getValueName()), calculated.get(CalculateConstant.START_INDEX.getValueName()))).append(NEW_LINE);
            }

            builder.append("---------------------------------------------------").append(NEW_LINE);

        } catch (NullPointerException e) {
            builder = new StringBuilder();
            builder
                    .append("[WARN] Please Check KimchiPaginator Usage.").append(NEW_LINE)
                    .append("[WARN] => KimchiPaginator paginator = new KimchiPaginator();").append(NEW_LINE)
                    .append("[WARN] => paginator.init({number-of-board-list}, 10, 10, currentPage, PaginatorConstant.ORACLE_PAGING);").append(NEW_LINE)
                    .append("[WARN] => paginator.elastic().paginate();").append(NEW_LINE)
                    .append("[WARN] => System.out.println(paginator.getPagingLog())").append(NEW_LINE)
                    .append("[WARN] URL : https://github.com/Kimchi-dev/kimchi-easy-paginator").append(NEW_LINE);
        }
        return builder.toString();
    }
}
