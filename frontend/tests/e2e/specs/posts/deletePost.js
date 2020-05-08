describe('Delete posts', () => {
    before(() => {
        cy.demoStudentLoginPosts();
        cy.gotoSubmitPost();
        cy.submitPost(' ', '[TEST§1] I dont understand');
        cy.demoTeacherLoginPosts();
        cy.gotoPosts();
        cy.answerPost('[TEST§1] I dont understand', '[ANSWER§1] This is an answer')
    });

    beforeEach(() => {
        cy.demoStudentLoginPosts();
    });

    afterEach(() => {
        cy.contains('Logout').parent().click();
    });

    it('delete submitted post', () => {
        cy.gotoPosts();
        cy.deletePost('[TEST§1] I dont understand');
    });
});
