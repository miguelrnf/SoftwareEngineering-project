describe('Post Buttons', () => {
    before(() => {
        cy.demoStudentLoginPosts();
        cy.gotoSubmitPost();
        cy.submitPost(' ', '[TEST§1] I dont understand');
        cy.demoTeacherLoginPosts();
        cy.gotoPosts();
        cy.answerPost('[TEST§1] I dont understand', '[ANSWER§1] This is an answer')
    });

    afterEach(() => {
        cy.contains('Logout').parent().click();
    });

    after(() => {
        cy.deletePost('[TEST§1] I dont understand');
    });

    it('list posts as student and press change discuss status button', () => {
        cy.demoStudentLoginPosts();
        cy.gotoPosts();
        cy.pressStatusButton('[TEST§1] I dont understand', 'DiscussStatusButton');
    });

    it('list posts as teacher and press change post status button', () => {
        cy.demoTeacherLoginPosts();
        cy.gotoPosts();
        cy.pressStatusButton('[TEST§1] I dont understand', 'PostStatusButton');
    });
});
