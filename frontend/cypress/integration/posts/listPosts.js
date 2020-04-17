describe('List Posts', () => {
    beforeEach(() => {
        cy.demoStudentLoginPosts();
        cy.gotoSubmitPost();
        cy.submitPost(' ', '[TEST§1] I dont understand');
    });

    afterEach(() => {
        cy.deletePost('[TEST§1] I dont understand');
        cy.contains('Logout').parent().click();
    });

    it('list posts as student', () => {
        cy.demoStudentLoginPosts();
        cy.gotoPosts();
    });

    it('list posts as teacher', () => {
        cy.demoTeacherLoginPosts();
        cy.gotoPosts();
    });

    it('list posts as student and press change discuss status button', () => {
        cy.demoStudentLoginPosts();
        cy.gotoPosts();
        cy.pressStatusButton('[TEST§1] I dont understand', 'DiscussStatusButton');
    });

    it('list posts as student and press change discuss status button', () => {
        cy.demoTeacherLoginPosts();
        cy.gotoPosts();
        cy.pressStatusButton('[TEST§1] I dont understand', 'PostStatusButton');
    });
});
