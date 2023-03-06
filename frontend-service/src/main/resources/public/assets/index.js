(function() {
  function updateHttpieFromSiblingAnchor(e) {
      const sessionId = document.querySelector("input[name=\"session_id\"]").value;
      const anchorSibling = e.target.parentElement.querySelector('a')
      document.querySelector("#httpie_command_area").value = `
          http -v :${anchorSibling.port}${anchorSibling.pathname} \\
              Cookie:SESSION=${sessionId}
      `.trim();
  }

  document.querySelectorAll("button[name=\"httpie\"]").forEach(button => {
      button.addEventListener('click', updateHttpieFromSiblingAnchor);
  });
})();
