describe('View Post', () => {
    before(() => {
        cy.demoStudentLoginPosts();
        cy.gotoSubmitPost();
        cy.submitPost('UtilityTree', '[TEST§1] I dont understand');
    });

    beforeEach(() => {
        cy.demoStudentLoginPosts();
    });

    afterEach(() => {
        cy.contains('Logout').parent().click();
    });

    it('check posts of a quiz', () => {
        cy.createQuiz();
        cy.checkPostsByQuiz();
    });
});
