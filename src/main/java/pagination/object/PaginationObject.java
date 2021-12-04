package pagination.object;


public interface PaginationObject {

    PaginationObject initialize(int totalContentsCount, int numPerPage, int contentsPerPage, int currentPage) throws Throwable;

    PaginationObject next();

    PaginationObject pre();

    /* Start DB Index Number */
    int getStartIndex();

    /* End DB Index Number */
    int getEndIndex();

    int getStartPage();

    int getEndPage();

    boolean isAbleToNextStep();

    boolean isAbleToPreviousStep();

    boolean hasNextPage();

    void ableToStep(boolean ableToPreviousStep, boolean ableToNextStep);

    void setHasNextPage(boolean hasNextPage);

    void setCurrentStepAndPage(int currentStep, int currentPage);

    PaginatedObject convertToPaginatedObject();

}
