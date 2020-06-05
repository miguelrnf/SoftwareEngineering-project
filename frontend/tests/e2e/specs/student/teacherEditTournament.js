describe('Teacher walkthrough', () => {
  beforeEach(() => {
    cy.demoTeacherLogin();
  });

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();
  });

  it('login and edit a Tournament', () => {
    cy.getTeacherTournament();
    cy.createTournament('Demo tournament', '30', false, false);
    cy.createTournament('Edited Demo tournament', '30', false, true);
    cy.deleteTournament('Edited Demo tournament');
  });
});
