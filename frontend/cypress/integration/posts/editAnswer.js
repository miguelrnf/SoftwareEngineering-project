describe('Edit Answer', () => {
    before(() => {
        cy.demoStudentLoginPosts();
        cy.gotoSubmitPost();
        cy.submitPost(' ', '[TEST§1] I dont understand');
        cy.demoTeacherLoginPosts();
        cy.gotoPosts();
        cy.answerPost('[TEST§1] I dont understand', '[ANSWER§1] This is an answer');
    });

    beforeEach(() => {
        cy.demoTeacherLoginPosts();
    });

    afterEach(() => {
        cy.contains('Logout').parent().click();
    });

    after(() => {
        cy.demoStudentLoginPosts();
        cy.gotoPosts();
        cy.deletePost('[TEST§1] I dont understand');
    });


    it('valid edit performed on answer', () => {
        cy.gotoPosts();
        cy.editAnswer('[TEST§1] I dont understand', '[ANSWER§2] This is an answer');
    });

    it('invalid edit performed on answer', () => {
        cy.gotoPosts();
        cy.editAnswer('[TEST§1] I dont understand', ' ');
    });
});
