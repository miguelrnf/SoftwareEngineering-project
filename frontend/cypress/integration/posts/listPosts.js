describe('List Posts', () => {
    afterEach(() => {
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

});
