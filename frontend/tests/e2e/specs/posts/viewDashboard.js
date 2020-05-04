describe('View Post', () => {
    before(() => {
        cy.demoStudentLoginPosts();
        cy.gotoSubmitPost();

        cy.submitPost(' ', '[TEST§1] I dont understand');
    });

    afterEach(() => {
        cy.contains('Logout').parent().click();
    });

    after(() => {
        cy.demoStudentLoginPosts();
        cy.deletePost('[TEST§1] I dont understand');
    });

    it('view dashboard', () => {
        cy.demoStudentLoginDashboard();
    });

});
