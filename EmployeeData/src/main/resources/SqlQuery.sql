SELECT *
FROM (
    SELECT *, DENSE_RANK() OVER (ORDER BY salary DESC) AS rank
    FROM employees
) ranked_employees
WHERE rank = N;

-- In place of N we can pass the required rank
