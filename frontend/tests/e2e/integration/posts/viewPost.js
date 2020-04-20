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
        cy.deletePost('[TEST§1] I dont understand');
    });

    it('view post as student', () => {
        cy.demoStudentLoginPosts();
        cy.gotoPosts();
        cy.viewPost('[TEST§1] I dont understand').type('{esc}', {force: true})
    });

    it('view post as teacher', () => {
        cy.demoTeacherLoginPosts();
        cy.gotoPosts();
        cy.viewPost('[TEST§1] I dont understand').type('{esc}', {force: true})
    });

});
