describe('Teacher walkthrough', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
  });

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();
  });

  it('login and creates a Tournament', () => {
    cy.getTeacherTournament();
    cy.createTournament('Demo tournament', '30', false, false);
    cy.deleteTournament('Demo tournament');
  });
});
