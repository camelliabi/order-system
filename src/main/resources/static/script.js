async function loadMenu() {
  const res = await fetch("/api/menu");
  const items = await res.json();

  const ul = document.getElementById("menuList");
  ul.innerHTML = "";

  items.forEach(item => {
    const li = document.createElement("li");
    li.textContent = item.itemName;
    ul.appendChild(li);
  });
}
loadMenu();
