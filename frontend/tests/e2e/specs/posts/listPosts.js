describe('List Posts', () => {
  it('list posts as student', () => {
    cy.demoStudentLoginPosts();
    cy.gotoPosts();
  });

  it('list posts as teacher', () => {
    cy.demoTeacherLoginPosts();
    cy.gotoPosts();
  });
});
