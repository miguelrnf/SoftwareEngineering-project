describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();
  });

  it('login and edit a Tournament', () => {
    cy.createTournament('Demo tournament', '30', true);
    cy.editTournament('Edited');
    cy.assertT('Demo tournamentEdited');
    cy.deleteTournament('Demo tournamentEdited');
  });
});
